package com.example.milky_way_back.Member.Entity;

public enum Role {
    STUDENT(Authority.STUDENT),
    MENTOR(Authority.MENTOR),
    ADMIN(Authority.ADMIN);

    private final String authority;

    // 역할마다 권한 다르게 부여
    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String STUDENT = "ROLE_STUDENT";
        public static final String MENTOR = "ROLE_MENTOR";
        public static final String ADMIN = "ROLE_ADMIN";
    }

}
