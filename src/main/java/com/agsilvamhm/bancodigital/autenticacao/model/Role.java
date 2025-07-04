package com.agsilvamhm.bancodigital.autenticacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private Long roleId;
    private String name;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
