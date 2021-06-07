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



            // 만약 연관관계에 있는 Team이 모두 다르다면 쿼리가 n+1번으로 나간다.
            String query = "select m from Member m ";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member=" +member.getUsername() +" , "+"team="+member.getTeam().getName());
                //회원1 , 팀 1(sql)
                //회원2 , 팀 2(1차 캐시)
                //회원3 , 팀 3(sql)
            }

            // 만약 연관관계에 있는 Team이 모두 다르다면 쿼리가 n+1번으로 나간다.
            // sql 작성에서 Eagar의 기능을 하는거와 같다 . join으로 한번에 진짜 데이터를 가져온다 .
            String query2 = "select m from Member m join fetch m.team";
            List<Member> result2 = em.createQuery(query2, Member.class)
                    .getResultList();
            for (Member member : result2) {
                System.out.println("member=" +member.getUsername() +" , "+"team="+member.getTeam().getName());
            }

            //반대편 일다다에서 컬랙션조회를 할떄
            //컬렉션 페치조인의 주의점
            //일대다 관계에서의 DB 레코드가 뻥튀기 된다 .
            // team2 에 2명이 있으니 결과도 2개가 나온다 . team2에 관한 내용도 멤버의 수만큼 나온다.
           String query3 = "select t from Team t join fetch t.members";
            List<Team> result3 = em.createQuery(query3, Team.class)
                    .getResultList();
            for (Team team : result3) {
                System.out.println("teamname=" +team.getName() +" , "+"member="+team.getMembers().size());
            }

            //위의 문제해결을 위해  JPQL이 distict 명령어를 제공한다 .
            //db의 distinct와는 조금 다르다 .
            //JPQL의 distinct는 sql에 distinct를 추가해서 날려주고
            //결과로 돌아온 값이 중복되는 엔티티를 한번더 체크해서 날려준다 .

            String query4 = "select distinct t from Team t join fetch t.members";
            List<Team> result4 = em.createQuery(query4, Team.class)
                    .getResultList();
            for (Team team : result4) {
                System.out.println("teamname=" +team.getName() +" , "+"member="+team.getMembers().size());
            }

            em.flush();
            em.clear();

            //일반 조인과 페치조인의 차이

            System.out.println("==============일반조인=============");
            //일반조인
            String query5 = "select  t from Team t join t.members";
            List<Team> result5 = em.createQuery(query5, Team.class)
                    .getResultList();
            for (Team team : result5) {
                System.out.println("teamname=" +team.getName() +" , "+"member="+team.getMembers().size());
            }

            System.out.println("==============페치조인=============");
            //페치조인
            String query6 = "select  t from Team t join fetch t.members";
            List<Team> result6 = em.createQuery(query6, Team.class)
                    .getResultList();
            for (Team team : result5) {
                System.out.println("teamname=" +team.getName() +" , "+"member="+team.getMembers().size());
            }






        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
