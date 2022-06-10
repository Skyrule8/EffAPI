package Effapi.Manga;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Manga {

    @Id
    @GeneratedValue
    public Long Id;

    @Column
    public String Nom;

    @Column
    public String Description;

    @Column
    public int Rating;

    @Column
    public int Tomes;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public int getTomes() {
        return Tomes;
    }

    public void setTomes(int tomes) {
        Tomes = tomes;
    }
}
