package com.olympic.mailParser.DAO.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sign_up_students")
@Data
@NoArgsConstructor
public class SignUpStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sing_up_id")
    private  Integer singUpID;

    @Column(name = "name")
    private  String name;

//    @Column (name = "phone")
//    String phone;
    
    @Column (name = "id_card")
    public String idCard;

}
