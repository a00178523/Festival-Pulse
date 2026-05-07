package com.ericsson.festivalpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FestivalArea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private String areaType;
    
    @Column(length = 10000)
    private String coordinates; // JSON array of {x, y} points
    
    private String baseColor; // Hex color for area identity
}
