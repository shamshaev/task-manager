package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Label implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = 3, max = 1000)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "taskLabels", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Task> tasks = new HashSet<>();

    @CreatedDate
    private LocalDate createdAt;

    @PrePersist
    public void createTaskAssociations() {
        for (var task : this.tasks) {
            task.getTaskLabels().add(this);
        }
    }

    @PreRemove
    public void removeTaskAssociations() {
        for (var task : this.tasks) {
            task.getTaskLabels().remove(this);
        }
    }
}
