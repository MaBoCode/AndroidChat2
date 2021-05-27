package com.example.app_template_java.core.user;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    public Long id;
    public String name;
    public String username;
    public String email;
    public String phone;

    public User(Long id, String name, String username, String email, String phone) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, email, phone);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public static class UserDiff extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull User oldItem, @NonNull @NotNull User newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull User oldItem, @NonNull @NotNull User newItem) {
            return oldItem.equals(newItem);
        }
    }
}