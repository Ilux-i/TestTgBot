package com.example.testTg_bot.moodle.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.time.LocalDateTime;

@Entity
@Table(schema = "tg_bot", name = "user_db")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public String toString(){
        return "User:{"+
                "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", userName=" + userName +
                ", registeredAt=" + registeredAt +
                "}";
    }
}


