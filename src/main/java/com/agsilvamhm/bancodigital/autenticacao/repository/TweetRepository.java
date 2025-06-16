package com.agsilvamhm.bancodigital.repository;

import com.agsilvamhm.bancodigital.autenticacao.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

}
