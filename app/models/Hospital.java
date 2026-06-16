//package models;
//
//import io.ebean.Model;
//import io.ebean.Finder;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "hospitals")
//public class Hospital extends Model {
//
//    @Id
//    private Long id;
//
//    @OneToOne
//    private User user;
//
//    private String hospitalName;
//
//    private String address;
//
//    private String city;
//
//    private String phone;
//
//    public static final Finder<Long, Hospital> find =
//            new Finder<>(Hospital.class);
//}