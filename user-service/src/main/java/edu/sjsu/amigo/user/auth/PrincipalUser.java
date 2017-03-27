/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package edu.sjsu.amigo.user.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 *
 * @author rwatsh on 3/27/17.
 */
@Data
public class PrincipalUser implements Principal {
    @NotEmpty
    @JsonProperty
    private String userEmail;
    @NotEmpty
    @JsonProperty
    private String password;

    public PrincipalUser(String user) {
        if (user == null) {
            throw new NullPointerException("illegal null input");
        }
        this.userEmail = user;
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    @Override
    public String getName() {
        return userEmail;
    }

    /**
     * Returns true if the specified subject is implied by this principal.
     * <p>
     * <p>The default implementation of this method returns true if
     * {@code subject} is non-null and contains at least one principal that
     * is equal to this principal.
     * <p>
     * <p>Subclasses may override this with a different implementation, if
     * necessary.
     *
     * @param subject the {@code Subject}
     * @return true if {@code subject} is non-null and is
     * implied by this principal, or false otherwise.
     * @since 1.8
     */
    @Override
    public boolean implies(Subject subject) {
        return true;
    }

    public String toString() {
        return("Principal:  " + userEmail);
    }


}
