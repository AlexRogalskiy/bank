package com.charges.db;

import com.charges.model.Client;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ClientRepository {
    @Select("SELECT * FROM client")
    List<Client> getClients();

    @Select("SELECT * FROM client WHERE id=#{clientId}")
    Client getClient(@Param("clientId") final Long clientId);

    @Insert("INSERT into client (name) values (#{name})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insert(final Client client);

    @Update("UPDATE client SET name=#{name} where id=#{id}")
    void update(final Client client);

    @Delete("DELETE client where id=#{clientId}")
    void delete(@Param("clientId") final Long clientId);
}