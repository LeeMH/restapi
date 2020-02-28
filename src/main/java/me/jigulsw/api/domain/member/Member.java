package me.jigulsw.api.domain.member;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Slf4j
@Getter@Setter
@Accessors(chain = true)
@DynamicInsert
@DynamicUpdate
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String mobile;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String birthday;

    @Column
    private String sex;

    @Column
    private boolean active;

    public String getDisplayName() {
        return StringUtils.isNotEmpty(name) ? name : StringUtils.right(mobile, 4);
    }

}
