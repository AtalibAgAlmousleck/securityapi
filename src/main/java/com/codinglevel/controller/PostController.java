package com.codinglevel.controller;

import com.codinglevel.entities.Post;
import com.codinglevel.enumerations.PostStatus;
import com.codinglevel.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/post")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @PostMapping(value = "/create")
    public String createPost(@RequestBody Post post, Principal principal) {
        post.setStatus(PostStatus.PENDING);
        post.setUserName(principal.getName());
        postRepository.save(post);
        return principal.getName() + " Your post published success , Required ADMIN/MODERATOR Action !";
    }

    @GetMapping("/approvePost/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approvePost(@PathVariable Long postId, Post post) {
        post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);
        return "Your Post Have been Approved Success";
    }

    @GetMapping(value = "/approveAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approveAll() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING))
                .forEach(post -> {
                    post.setStatus(PostStatus.APPROVED);
                    postRepository.save(post);
                });
        return "Approve Post";
    }

    @GetMapping("/removePost/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removePost(@PathVariable Long postId, Post post) {
        post = postRepository.findById(postId).get();
        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);
        return "Your Post Have been Rejected";
    }

    @GetMapping(value = "/rejectAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String rejectAll() {
        postRepository.findAll().stream().filter(post -> post.getStatus().equals(PostStatus.PENDING))
                .forEach(post -> {
                    post.setStatus(PostStatus.REJECTED);
                    postRepository.save(post);
                });
        return "Reject All Posts";
    }

    @GetMapping(value = "/viewAll")
    public List<Post> viewAll() {
        return postRepository.findAll().stream().filter(post -> post.getStatus()
                .equals(PostStatus.APPROVED)).collect(Collectors.toList());
    }
}
