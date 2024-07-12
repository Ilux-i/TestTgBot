package com.example.testTg_bot.moodle.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "ads_table")
@Table(schema = "tg_bot", name = "ads_table")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ad;

}
