package com.campushub.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "secretariats")
public class Secretariat extends User {
    // This class can be expanded with specific attributes for secretariats in the future.
}
