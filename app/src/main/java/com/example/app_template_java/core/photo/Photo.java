package com.example.app_template_java.core.photo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class Photo implements Serializable {

    public Long id;
    public String title;
    public String thumbnailUrl;

    public Photo(Long id, String title, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id) &&
                Objects.equals(title, photo.title) &&
                Objects.equals(thumbnailUrl, photo.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, thumbnailUrl);
    }

    public static class PhotoDiff extends DiffUtil.ItemCallback<Photo> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Photo oldItem, @NonNull @NotNull Photo newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Photo oldItem, @NonNull @NotNull Photo newItem) {
            return oldItem.equals(newItem);
        }
    }
}
