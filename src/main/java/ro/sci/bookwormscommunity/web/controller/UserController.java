package ro.sci.bookwormscommunity.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.sci.bookwormscommunity.mapper.UserMapper;
import ro.sci.bookwormscommunity.model.Book;
import ro.sci.bookwormscommunity.model.User;
import ro.sci.bookwormscommunity.service.BookService;
import ro.sci.bookwormscommunity.service.UserService;
import ro.sci.bookwormscommunity.web.dto.UserDto;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/**")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @ModelAttribute("user")
    public UserDto userDto() {
        return new UserDto();
    }

    @GetMapping
    public String userProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Book> books = bookService.getUserBooks(user.getId());
        model.addAttribute("books", books);
        model.addAttribute("user", user);
        model.addAttribute("principal", principal);
        return "user";
    }

    @GetMapping("/user/{id}")
    public String seeUserProfile(@PathVariable("id") long id, Model model, Principal principal) {
        User user = userService.findById(id);
        List<Book> books = bookService.getUserBooks(user.getId());
        model.addAttribute("books", books);
        model.addAttribute("user", user);
        model.addAttribute("principal", principal);
        return "user";
    }

    @GetMapping("/user/editProfile")
    public String showUpdateForm(@ModelAttribute("user") UserDto userDto, Principal principal, Model model) throws IOException {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", UserMapper.mapUserToUserDto(user, userDto));
        return "updateUser";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") @Valid UserDto userDto, Principal principal, BindingResult result) throws Exception {
        User user = userService.findByEmail(principal.getName());

        if (!principal.getName().equals(userDto.getEmail())) {
            User existing = userService.findByEmail(userDto.getEmail());
            if (existing != null) {
                result.rejectValue("email", null, "There is already an account registered with that email");
            }
        }

        if (result.hasErrors()) {
            return "updateUser";
        }

        userService.updateUser(user.getId(), userDto);

        if (!principal.getName().equals(userDto.getEmail()) || !userDto.getPassword().isEmpty()) {
            return "redirect:/logout";
        }

        return "redirect:/user";
    }
}

