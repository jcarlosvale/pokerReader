package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Player {

    public static final String PREFIX_TABLE = "player_";

    @Id
    @Column(name = PREFIX_TABLE + "nickname")
    private String nickname;

    @OneToMany(mappedBy = "player", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<Seat> seats;
}
