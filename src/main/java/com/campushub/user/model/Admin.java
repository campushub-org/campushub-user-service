package com.campushub.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {
    // This class can be expanded with specific attributes for admins in the future.
}
