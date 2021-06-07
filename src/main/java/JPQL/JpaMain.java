package JPQL;


import JPQL.domain.Address;
import JPQL.domain.Member;
import JPQL.domain.MemberDto;
import JPQL.domain.Team;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team1 =  new Team("team1");
            Team team2 =  new Team("team2");
            em.persist(team1);
            em.persist(team2);


            //샘플 데이터
            Member member1 = new Member("user1",10);
            member1.setTeam(team1);
            Member member2 = new Member("user2",10);
            member2.setTeam(team2);
            Member member3 = new Member("user3",10);
            member3.setTeam(team2);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            em.flush();
            em.clear();


            // 상태 필드 표현
            // 더 이상 객체 탐색이 불가능하다
            String query = "select m.username from Member m";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();


            //단일값 연관 필드
            //team 은 엔티티 이기 떄문에 팀에서 한번더 탐색이 가능하다 .
            //m.team 에서 묵시적 내부조인이 일어나고 name을 찾을때는 상태필드기 때문에 탐색이 끝난다.
            //JPQL 에서는 Join 문을 적지 않아도 연관관계에 의해 묵시적으로 조인이 일어나고 SQL에는 join문이 나가기 떄문에
            // JPQL에서도 SQL 과 마찬가지로 Join문을 명시적으로 써주는 것이 좋다 .
            String query1 = "select m.team.name from Member m";
            List<Member> result1 = em.createQuery(query1, Member.class)
                    .getResultList();

            //컬랙션값 연관경로
            //컬렉션값 연관경로는 컬렉션에 몇번째 값을 원하는지에 대해 알수 없음으로 탐색을할 수 없다
            String query2 = "select t.members from Team t";
            Collection result2 = em.createQuery(query2, Collection.class)
                    .getResultList();

            //컬랙션값 연관경로 해결 (명시적 조인 사용)
            //명시적 조인을 하면 별칭을 얻을 수 있기 떄문에 탐색이 가능하다 .
            String query3 = "select t.members,m.username from Team t join t.members m ";
            Collection result3 = em.createQuery(query3, Collection.class)
                    .getResultList();




        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
