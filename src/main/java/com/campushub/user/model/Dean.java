package com.campushub.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "deans")
public class Dean extends Teacher {
    // This class can be expanded with specific attributes for deans in the future.
}
