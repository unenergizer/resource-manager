package com.forgestorm.resourcemanager.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Controller
public class LoginController {

    private static final long NO_USER_FOUND = -1;

    @RequestMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String index(@RequestParam String username, @RequestParam String password) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        long userId = checkUsernameExists(username);
        boolean passwordMatch = false;

        if (userId != NO_USER_FOUND) {
            // 1. Get the password blob from the database
            Blob passwordBlob = getUserPasswordById(userId);

            // 2. Convert the Blob to a string and cut the end off
            String string = passwordBlobToString(passwordBlob);
            String hash = string.substring(22, string.length() - 3);

            // 3. Get the results for a match.
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
            passwordMatch = result.verified;
        }

        if (passwordMatch) {
            System.out.println("Login was successful!");
            return "redirect:/dashboard";
        } else {
            System.out.println("Incorrect username/password combo. Username/Password: " + username + "/" + password);
            return "login";
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Checks to see if the supplied username exists in the database.
     *
     * @param username The username in the login form.
     * @return The userId of the user.
     */
    private long checkUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM xf_user WHERE username = ?";
        List<Long> results = jdbcTemplate.queryForList(sql, Long.class, username);
        return results.isEmpty() ? NO_USER_FOUND : results.get(0);
    }

    /**
     * Gets the password blob from the database.
     *
     * @param userId The user's userId.
     * @return The blob that stores the user's password.
     */
    private Blob getUserPasswordById(long userId) {
        String sql = "SELECT data FROM xf_user_authenticate WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Blob.class, userId);
    }

    /**
     * Process the password blob. Converts it to a {@link String}
     *
     * @param passwordBlob The password blob to convert.
     * @return A {@link String} that represents the password hash
     */
    private String passwordBlobToString(Blob passwordBlob) {
        try (InputStream inputStream = passwordBlob.getBinaryStream()) {

            int character;
            StringBuilder stringBuilder = new StringBuilder();

            while ((character = inputStream.read()) != -1) {
                stringBuilder.append((char) character);
            }

            return stringBuilder.toString();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("");
    }
}
