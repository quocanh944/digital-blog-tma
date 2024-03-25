package com.tma.digital_blog.service.impl;

import com.tma.digital_blog.dto.mapper.UserMapper;
import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.Comment;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.dto.UserDTO;
import com.tma.digital_blog.repository.ArticleRepository;
import com.tma.digital_blog.repository.CommentRepository;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.service.UserService;
import com.tma.digital_blog.util.NotFoundException;
import com.tma.digital_blog.util.ReferencedWarning;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Override
    public Page<UserDTO> findAll(Integer page, Integer size, String search) {

        return userRepository
                .findUsersByUsernameContainingIgnoreCase(search, PageRequest.of(page, size))
                .map(user -> UserMapper.mapToDTO(user, new UserDTO()));
    }

    @Override
    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> UserMapper.mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public UserDTO get(final String username) {
        return userRepository.findUserByUsername(username)
                .map(user -> UserMapper.mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final UserDTO userDTO) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final User user = new User();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        UserMapper.mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    @Override
    public void activate(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        user.setActivated(true);
        userRepository.save(user);
    }

    @Override
    public void deactivate(Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        user.setActivated(false);
        userRepository.save(user);
    }

    @Override
    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean usernameExists(final String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Article createdByArticle = articleRepository.findFirstByCreatedBy(user);
        if (createdByArticle != null) {
            referencedWarning.setKey("user.article.createdBy.referenced");
            referencedWarning.addParam(createdByArticle.getId());
            return referencedWarning;
        }
        final Comment usersIdComment = commentRepository.findFirstByUser(user);
        if (usersIdComment != null) {
            referencedWarning.setKey("user.comment.usersId.referenced");
            referencedWarning.addParam(usersIdComment.getId());
            return referencedWarning;
        }
        return null;
    }

}
