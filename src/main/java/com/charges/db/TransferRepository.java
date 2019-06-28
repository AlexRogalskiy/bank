package com.charges.db;

import com.charges.model.Transfer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransferRepository {
    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "accountNumberTo",
                    column = "to_account_id",
                    javaType = String.class,
                    one = @One(select = "com.charges.db.AccountRepository.getAccountNumber")),
            @Result(property = "accountNumberFrom",
                    column = "from_account_id",
                    javaType = String.class,
                    one = @One(select = "com.charges.db.AccountRepository.getAccountNumber"))
    })
    @Select("SELECT * FROM transfer WHERE client_id=#{clientId} and id=#{transferId}")
    Transfer getTransferByIdAndClientId(@Param("clientId") final Long clientId, @Param("transferId") final Long transferId);

    @Results(value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "accountNumberTo",
                    column = "to_account_id",
                    javaType = String.class,
                    one = @One(select = "com.charges.db.AccountRepository.getAccountNumber")),
            @Result(property = "accountNumberFrom",
                    column = "from_account_id",
                    javaType = String.class,
                    one = @One(select = "com.charges.db.AccountRepository.getAccountNumber"))})
    @Select("SELECT * FROM transfer WHERE client_id=#{clientId}")
    List<Transfer> getTransfersByClientId(@Param("clientId") final Long clientId);

    @Insert({"INSERT INTO transfer (amount,to_account_id, from_account_id, client_id) values",
            " (#{transfer.amount}, #{toAccountId}, #{fromAccountId}, #{transfer.clientId})"})
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "transfer.id")
    void addTransfer(@Param("transfer") final Transfer transfer,
                     @Param("fromAccountId") final Long fromAccountId,
                     @Param("toAccountId") final Long toAccountId);
}