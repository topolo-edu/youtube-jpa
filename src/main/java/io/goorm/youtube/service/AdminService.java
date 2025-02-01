package io.goorm.youtube.service;


import io.goorm.youtube.domain.Admin;

import java.util.List;
import java.util.Optional;


public interface AdminService {

    public List<Admin> findAll();

    public Optional<Admin> find(Long adminSeq);

    public Admin login(Admin admin);

    public Admin save(Admin admin);

    public Admin update(Long adminSeq,Admin admin);

    public Admin remove(Admin admin);

    public Admin updateUseYn(Long adminSeq);

}
