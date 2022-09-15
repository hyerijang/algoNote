package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    private final EntityManager em;

    public Tag save(Tag tag) {
        em.persist(tag);
        return tag;
    }

    public Tag findById(Long id) {
        return em.find(Tag.class, id);
    }

    public Tag findByName(String name) {
        List<Tag> results = em.createQuery("select  t from Tag  t where  t.name = :name",
                        Tag.class)
                .setParameter("name", name)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }


    public List<Tag> findAll() {
        return em.createQuery("select t from Tag t", Tag.class).getResultList();
    }

}
