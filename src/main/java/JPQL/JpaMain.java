package JPQL;


import JPQL.domain.Address;
import JPQL.domain.Member;
import JPQL.domain.MemberDto;
import JPQL.domain.Team;
import jdk.swing.interop.SwingInterOpUtils;

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
            Team team3 =  new Team("team3");
            em.persist(team1);
            em.persist(team2);
            em.persist(team3);


            //샘플 데이터
            Member member1 = new Member("user1",10);
            member1.setTeam(team1);
            Member member2 = new Member("user2",10);
            member2.setTeam(team1);
            Member member3 = new Member("user3",10);
            member3.setTeam(team2);
            Member member4 = new Member("user4",10);
            member4.setTeam(team2);
            Member member5 = new Member("user5",10);
            member5.setTeam(team3);
            Member member6 = new Member("user6",10);
            member6.setTeam(team3);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);
            em.persist(member5);
            em.persist(member6);

            //벌크연산은 영속성 컨텍스트를 무시한다
            em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            //아래의 문제 때문에 벌크연산 후에는 영속성을 초기화해주는 것이좋다.
            //영속성 컨텍스트가 초기화 되었기떄문에 DB에서 값을 가져오고 영속성 컨텍스트에 다시 올릴 것이다.
            em.clear();
            em.flush();

            //DB에는 update 되었지만 조회시에는 영속성컨텍스트에 저장된 것을 가져온다
            Member member = em.find(Member.class, member1.getId());
            System.out.println("member.getAge() = " + member.getAge());



            tx.commit();

        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
