package com.agsilvamhm.bancodigital.model;

public enum Values {
    ADMIN(1l),
    CLIENTE(2l);

    long roleId;

    Values(long roleId){
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
