package com.example.dsl;

import com.example.dsl.domain.Member;
import com.example.dsl.domain.QMember;
import com.example.dsl.domain.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GradleApplicationTests {
	@Autowired
	TestEntityManager testEntityManager;

	@Autowired EntityManager em;
	JPAQueryFactory jpaQueryFactory;


	@BeforeEach
	void init() {
		em = testEntityManager.getEntityManager();
		jpaQueryFactory = new JPAQueryFactory(em);

		Team team1 = new Team("team1");
		em.persist(team1);
		em.persist(new Member("member1", 10, team1) );
		em.flush();
		em.clear();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void startJPQL() {
		Member findByJPQL = em.createQuery(
						"select m from Member m " +
								"where m.username = :username", Member.class)
				.setParameter("username", "member1")
				.getSingleResult();

		assertThat(findByJPQL.getUsername()).isEqualTo("member1");
	}

	@Test
	void startQuerydsl() {
		QMember m = new QMember("m");

		Member findMember = jpaQueryFactory
				.select(m)
				.from(m)
				.where(m.username.eq("member1"))
				.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

}
