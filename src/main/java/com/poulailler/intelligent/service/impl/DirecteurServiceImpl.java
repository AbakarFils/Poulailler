package com.poulailler.intelligent.service.impl;

import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.repository.DirecteurRepository;
import com.poulailler.intelligent.repository.UserRepository;
import com.poulailler.intelligent.service.DirecteurService;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.service.mapper.DirecteurMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Directeur}.
 */
@Service
@Transactional
public class DirecteurServiceImpl implements DirecteurService {

    private final Logger log = LoggerFactory.getLogger(DirecteurServiceImpl.class);

    private final DirecteurRepository directeurRepository;

    private final DirecteurMapper directeurMapper;

    private final UserRepository userRepository;

    public DirecteurServiceImpl(DirecteurRepository directeurRepository, DirecteurMapper directeurMapper, UserRepository userRepository) {
        this.directeurRepository = directeurRepository;
        this.directeurMapper = directeurMapper;
        this.userRepository = userRepository;
    }

    @Override
    public DirecteurDTO save(DirecteurDTO directeurDTO) {
        log.debug("Request to save Directeur : {}", directeurDTO);
        Directeur directeur = directeurMapper.toEntity(directeurDTO);
        Long userId = directeurDTO.getUser().getId();
        userRepository.findById(userId).ifPresent(directeur::user);
        directeur = directeurRepository.save(directeur);
        return directeurMapper.toDto(directeur);
    }

    @Override
    public Optional<DirecteurDTO> partialUpdate(DirecteurDTO directeurDTO) {
        log.debug("Request to partially update Directeur : {}", directeurDTO);

        return directeurRepository
            .findById(directeurDTO.getId())
            .map(existingDirecteur -> {
                directeurMapper.partialUpdate(existingDirecteur, directeurDTO);

                return existingDirecteur;
            })
            .map(directeurRepository::save)
            .map(directeurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DirecteurDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Directeurs");
        return directeurRepository.findAll(pageable).map(directeurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DirecteurDTO> findOne(Long id) {
        log.debug("Request to get Directeur : {}", id);
        return directeurRepository.findById(id).map(directeurMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Directeur : {}", id);
        directeurRepository.deleteById(id);
    }
}
