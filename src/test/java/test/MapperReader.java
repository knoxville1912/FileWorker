package test;

import program.ReadMapper;

public class MapperReader implements ReadMapper<User> {
    @Override
    public User map(String[] params) {
        return new User(
                Long.parseLong(params[0]),
                params[1],
                params[2]
        );
    }
}
