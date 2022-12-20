package antifraud.entity.converter;

import antifraud.entity.enums.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.getPriority();
    }

    @Override
    public Role convertToEntityAttribute(Integer priority) {
        if (priority == null) {
            return null;
        }

        return Stream.of(Role.values())
                .filter(c -> c.getPriority().equals(priority))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }
}
