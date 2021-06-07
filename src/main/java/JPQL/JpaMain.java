package JPQL;


import JPQL.domain.Address;
import JPQL.domain.Member;
import JPQL.domain.MemberDto;
import JPQL.domain.Team;

import javax.persistence.*;
import java.util.List;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team =  new Team("team1");
            em.persist(team);

            //샘플 데이터
            Member member = new Member("user",10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();


            // inner join (inner 생략가능)
            String query = "select m from Member m inner join m.team t";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            // left outer join (outer 생략가능)
            String query2 = "select m from Member m left outer join m.team t";
            List<Member> result2 = em.createQuery(query2, Member.class)
                    .getResultList();

            // on 절로 join의 조건 추가
            String query3 = "select m from Member m inner join m.team t on  t.name = 'team1'";
            List<Member> result3 = em.createQuery(query3, Member.class)
                    .getResultList();

            //연관관계가 없는 엔티티 외부 조인
            String query4 = "select m from Member m left join Team t on m.username = t.name ";
            List<Member> result4 = em.createQuery(query4, Member.class)
                    .getResultList();



        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
