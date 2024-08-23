package hexlet.code.mapper;

import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Mapping(source = "password", target = "passwordDigest")
    @BeanMapping(qualifiedByName = "encrypt")
    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);

    public abstract User map(UserDTO dto);

    @Mapping(source = "password", target = "passwordDigest")
    @BeanMapping(qualifiedByName = "encrypt")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    @AfterMapping
    @Named("encrypt")
    public void encryptPassword(@MappingTarget User model) {
        var password = model.getPasswordDigest();
        model.setPasswordDigest(passwordEncoder.encode(password));
    }
}
