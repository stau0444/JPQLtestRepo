package JPQL;


import JPQL.domain.Address;
import JPQL.domain.Member;
import JPQL.domain.MemberDto;

import javax.persistence.*;
import java.util.List;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //샘플 데이터
            for (int i = 0 ; i <100; i++){
                Member member = new Member("user"+i , i);
                em.persist(member);
            }

            em.flush();
            em.clear();


            //페이징
            List<Member> result = em.createQuery("select m from Member  m order by m.age desc", Member.class)
                    .setFirstResult(0) // offset
                    .setMaxResults(10) // limit
                    .getResultList();

            System.out.println("result.size() = " + result.size());

            result.forEach(System.out::println);



        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
