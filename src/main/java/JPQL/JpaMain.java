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
            em.persist(team1);
            em.persist(team2);


            //샘플 데이터
            Member member1 = new Member("user1",10);
            member1.setTeam(team1);
            Member member2 = new Member("user2",10);
            member2.setTeam(team2);
            Member member3 = new Member("user3",10);
            member3.setTeam(team2);
            Member member4 = new Member("user4",10);
            member4.setTeam(team2);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);

            em.flush();
            em.clear();



            //페치조인의 한계
            //1.기본적으로 연결된 것을 모두 끌어오는 것이기 떄문에
            //페치조인 되는 대상에는 별칭을 주지 않는 것이 관례이다.
            //t.members 에서 필터링을 해서 원하는 만큼만 가져오고싶다면?
            //아예 팀이아닌 멤버에서 조회를 해야한다.

            //2. 둘이상의 컬렉션은 패치 조인 할 수 없다.
            //일대다 관계만해도 데이터가 뻥튀기 되는데 , 컬렉션을 한번더 가져오면 일대다대다 관계가 되고
            // 뻥튀기에 뻥튀기가 됨

            //3.컬렉션을 페치 조인하면 페이징 API를 사용할 수 없다.
            // 일대다 관계에서 데이터가 뻥튀기 되는데 페이징을 해버리면
            // 데이터가 짤려버릴 수 있다.
            //컬렉션 패치 조인에서 페이징 api를 사용하면 in-memory로 동작하기 떄문에 매우 위험하다
            //3


//            String query = "select  t from Team t join fetch t.members as tm";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .setFirstResult(0)
//                    .setMaxResults(2)
//                    .getResultList();
//            for (Team team : result) {
//                System.out.println("teamname=" +team.getName() +" , "+"member="+team.getMembers().size());
//            }

            System.out.println("________________페치조인페이징_________________");
            //페치조인 페이징 해결 방안 2가지

            //1. 반대로 접근한다 .
            //멤버로 조회하여 멤버의 팀을가져온다
            //다대일로 연관관계가 바뀌기 때문에 페이징이 가능해진다.
            String query1 = "select  m from Member m  join fetch m.team";
            List<Member> result1 = em.createQuery(query1, Member.class)
                    .setFirstResult(0)
                    .setMaxResults(4)
                    .getResultList();
            for (Member member : result1) {
                System.out.println("team->"+member.getTeam().getName()+", member->"+member.getUsername());
            }

            System.out.println("------------------Test----------------");



            String query3 = "select  t from Team t";
            List<Team> result3 = em.createQuery(query3, Team.class)
                    .getResultList();
            System.out.println("result3.size() = " + result3.size());

            for (Team team : result3) {
                System.out.println("team="+team.getName()+",members="+team.getMembers().size());
                for (Member member : team.getMembers()){
                    System.out.println("->member=" + member);
                }
            }
            //정리

            //1대다 관계에서 다쪽은 1의 고유키를 공유하고 있다 .
            //예를들면 팀 1 -> 회원 1, 회원2
            //회원 1과 회원2 의 외래키는 팀1의 고유키일 것이다.

            //이때문에 1대다 관계의 조회시 데이터의 뻥튀기가 일어난다 .
            //팀1에서 멤버를 연관된 멤버를 조회한다고 생각해보자
            //select * from team t join member m on m.team_id = t.id;
            //join 조건을보면 m.team_id = t.id 외래키인 팀아이디를 비교한다.
            //t.id 를 team_id로 갖고있는 회원은 현재 회원1 , 회원2기 떄문에
            //1 쪽인 팀의 입장에서 다 쪽인 회원을 연관관계로 찾아낼경우
            //join 조건을 통해  조회되는 레코드가 2개가 될 것이다 (회원 1 , 회원2가 팀1의 아이디를 갖고 있음 )
            //그렇다면 JPA 입장에서는 ? 팀이 두개가 되어버리고 두개는 같은 팀의 주소 값을 가르킨다.
            //






            tx.commit();

        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
