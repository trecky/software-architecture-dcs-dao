package br.edu.utfpr.dao;

import br.edu.utfpr.dto.ClientDTO;
import br.edu.utfpr.dto.CountryDTO;
import lombok.extern.java.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class ClientDAO {

    private static final String DEFAULT_URL = "jdbc:derby:memory:database;create=true";
    private static final String INSERT = "INSERT INTO cliente (id,nome,telefone,idade,limiteCredito,id_pais) VALUES(?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE cliente SET nome=?, telefone=?,idade=?,limiteCredito=?,id_pais=? WHERE id=?";
    private static final String DELETE = "DELETE FROM cliente WHERE id = ?";
    private static final String SELECT_ID = "SELECT * FROM cliente JOIN pais ON id_pais = pais.id WHERE ID = ?";
    private static final String SELECT_ALL = "SELECT * FROM cliente JOIN pais ON id_pais = pais.id";

    public ClientDAO() {
        try (Connection conn = DriverManager.getConnection(DEFAULT_URL)) {
            conn.createStatement().executeUpdate(
                    "CREATE TABLE cliente (" +
                            "id int NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT id_cliente_pk PRIMARY KEY," +
                            "nome varchar(255)," +
                            "telefone varchar(30)," +
                            "idade int," +
                            "limiteCredito double," +
                            "id_pais int)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(ClientDTO client) {

        try (PreparedStatement pst = DriverManager.getConnection(DEFAULT_URL).prepareStatement(INSERT)) {

            pst.setInt(1, client.getId());
            pst.setString(2, client.getName());
            pst.setString(3, client.getTelephone());
            pst.setInt(4, client.getAge());
            pst.setDouble(5, client.getCreditLimit());
            pst.setInt(6, client.getCountry().getId());
            pst.execute();
            log.info("Insert successfull");

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void update(ClientDTO client) {

        try (PreparedStatement pst = DriverManager.getConnection(DEFAULT_URL).prepareStatement(UPDATE)) {

            pst.setString(1, client.getName());
            pst.setString(2, client.getTelephone());
            pst.setInt(3, client.getAge());
            pst.setDouble(4, client.getCreditLimit());
            pst.setInt(5, client.getCountry().getId());
            pst.setInt(6, client.getId());

            final int recordUpdated = pst.executeUpdate();
            log.info("Records Update: " + recordUpdated);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    public ClientDTO read(int clientId) {
        try (PreparedStatement pst = DriverManager.getConnection(DEFAULT_URL).prepareStatement(SELECT_ID)) {

            pst.setInt(1, clientId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return ClientDTO
                        .builder()
                        .id(rs.getInt("cliente.id"))
                        .name(rs.getString("cliente.nome"))
                        .telephone(rs.getString("telefone"))
                        .age(rs.getInt("idade"))
                        .creditLimit(rs.getDouble("limiteCredito"))
                        .country(CountryDTO
                                .builder()
                                .id(rs.getInt("pais.id"))
                                .name(rs.getString("pais.nome"))
                                .abbrev(rs.getString("sigla"))
                                .code(rs.getInt("codigoTelefone"))
                                .build())
                        .build();
            } else {
                log.info("No Records for Id Client: " + clientId);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    public void delete(int id) {
        try (PreparedStatement pst = DriverManager.getConnection(DEFAULT_URL).prepareStatement(DELETE)) {

            pst.setInt(1, id);

            final int recordDeleted = pst.executeUpdate();
            log.info("Records Deleted: " + recordDeleted);

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    public List<ClientDTO> selectAll() {
        try (PreparedStatement pst = DriverManager.getConnection(DEFAULT_URL).prepareStatement(SELECT_ALL)) {

            List<ClientDTO> clients = new ArrayList<>();

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                clients.add(ClientDTO
                        .builder()
                        .id(rs.getInt("cliente.id"))
                        .name(rs.getString("cliente.nome"))
                        .telephone(rs.getString("telefone"))
                        .age(rs.getInt("idade"))
                        .creditLimit(rs.getDouble("limiteCredito"))
                        .country(CountryDTO
                                .builder()
                                .id(rs.getInt("pais.id"))
                                .name(rs.getString("pais.nome"))
                                .abbrev(rs.getString("sigla"))
                                .code(rs.getInt("codigoTelefone"))
                                .build())
                        .build());
            }
            return clients;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
}
