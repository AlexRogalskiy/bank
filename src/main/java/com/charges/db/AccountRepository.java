package com.charges.db;

import com.charges.model.Account;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountRepository {

    @Select("SELECT number FROM account WHERE id=#{accountId}")
    String getAccountNumber(@Param("accountId") final Long accountId);

    @Select("SELECT * FROM account WHERE client_id=#{clientId}")
    List<Account> getAccounts(@Param("clientId") final Long clientId);

    @Select("SELECT * FROM account WHERE id=#{accountId} FOR UPDATE")
    Account selectForUpdate(@Param("accountId") final Long accountId);

    @Select("SELECT * FROM account WHERE number=#{accountNumber}")
    Account getAccountByNumber(@Param("accountNumber") final String accountNumber);

    @Select("SELECT * FROM account WHERE id=#{accountId}")
    Account selectAccount(@Param("accountId") final Long accountId);

    @Update("UPDATE account SET balance=#{balance} WHERE id=#{accountId}")
    void updateBalance(@Param("accountId") final Long accountId,
                       @Param("balance") final BigDecimal balance);

    @Insert("INSERT into account (number, client_id) values (#{number}, #{clientId})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertAccount(final Account account);

    @Update("UPDATE account SET number=#{number},client_id= #{clientId}, modified_on=now() where id=#{id}")
    void updateAccount(final Account account);

    @Delete("DELETE account where id=#{accountId}")
    void deleteAccount(@Param("accountId") final Long accountId);
}
