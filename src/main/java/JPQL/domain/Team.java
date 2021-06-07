package JPQL.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

   @Id @GeneratedValue
   private Long id;

   private String name;

   @OneToMany(mappedBy = "team")
   private List<Member> members;

    public Team(String name) {
        this.name = name;
    }
}
