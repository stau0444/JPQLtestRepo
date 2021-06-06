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
            Member member = new Member("user1" , 20);
            em.persist(member);

            em.flush();
            em.clear();

            //스칼라 타입에서 값을 받는 방법
            em.createQuery("select m.username,m.age from Member m ")
                    .getResultList();

            //1. Object [] 타입으로 조회
            List<Object[]> resultList = em.createQuery("select m.username,m.age from Member m ")
                    .getResultList();

            Object[] objects = resultList.get(0);
            System.out.println("objects[0] = " + objects[0]);
            System.out.println("objects[1] = " + objects[1]);
            tx.commit();

            //2.new 명령어로 Dto 생성하여 조회

            List<MemberDto> memberDto = em.createQuery("select new JPQL.domain.MemberDto( m.username,m.age) from Member m ", MemberDto.class)
                    .getResultList();

            MemberDto memberDto1 = memberDto.get(0);
            System.out.println("memberDto1.getUsername() = " + memberDto1.getUsername());
            System.out.println("memberDto1.getAge() = " + memberDto1.getAge());

        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
