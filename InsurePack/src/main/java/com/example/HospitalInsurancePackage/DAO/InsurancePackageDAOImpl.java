package com.example.HospitalInsurancePackage.DAO;

import com.example.HospitalInsurancePackage.Contractors.InsurancePackageDAO;
import com.example.HospitalInsurancePackage.Mappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import com.example.HospitalInsurancePackage.model.DiseaseDetails;
import com.example.HospitalInsurancePackage.model.InsurancePackage;
import com.example.HospitalInsurancePackage.model.InsurancePackageCoveredDisease;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InsurancePackageDAOImpl implements InsurancePackageDAO {

	private final JdbcTemplate jdbcTemplate;
	private static final String GET_INSURANCE_PACKAGES = "SELECT * FROM InsurancePackages";
	private static final String GET_COVERED_DISEASES_BY_PACKAGE_ID = "SELECT * FROM InsurancePackageCoveredDiseases WHERE insp_id = ?";
	private static final String GET_DISEASE_DETAILS_BY_DISEASE_ID = "select * from Diseases where disc_id=?";
	private static final String GET_FILTERED_PACKAGES = "select * FROM InsurancePackages where insp_status=? and ? BETWEEN insp_agelimit_start AND insp_agelimit_end;";
	private static final String GET_PACKAGES_BY_STATUS = "select * FROM InsurancePackages where insp_status=?";
	private static final String GET_FILTERED_PACKAGES_BY_AGE = "select * FROM InsurancePackages where ? BETWEEN insp_agelimit_start AND insp_agelimit_end;";
	private static final String GET_DISEASE_DETAILS_BY_PACKAGE_ID = "SELECT D.* FROM Diseases AS D JOIN InsurancePackageCoveredDiseases AS IPCD ON D.disc_id = IPCD.disc_id JOIN InsurancePackages AS IP ON IPCD.insp_id = IP.insp_id WHERE IP.insp_id = ?;";

	@Autowired
	public InsurancePackageDAOImpl(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<InsurancePackage> getAllInsurancePackages() {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(GET_INSURANCE_PACKAGES, new InsurancePackageRowMapper());
	}

	@SuppressWarnings("deprecation")
	public List<InsurancePackageCoveredDisease> getCoveredDiseasesByPackageId(int packageId) {
		return jdbcTemplate.query(GET_COVERED_DISEASES_BY_PACKAGE_ID, new Object[] { packageId },
				new InsurancePackageCoveredDiseaseRowMapper());
	}

	@SuppressWarnings("deprecation")
	@Override
	public DiseaseDetails getDetailsByDiseaseId(int discId) {
		// TODO Auto-generated method stub
		return jdbcTemplate.queryForObject(GET_DISEASE_DETAILS_BY_DISEASE_ID, new Object[] { discId },
				new DiseseDetailsRowMapper());
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<InsurancePackage> getFiteredDiseases(String status, int age) {
		// TODO Auto-generated method stub
		System.out.println("dao" + status + age);
		// System.out.println(jdbcTemplate.query("select * FROM InsurancePackages",
		// values, new InsurancePackageRowMapper()));
		return jdbcTemplate.query(GET_FILTERED_PACKAGES, new Object[] { status, age }, new InsurancePackageRowMapper());
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<InsurancePackage> getPackagesByStatus(String status) {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(GET_PACKAGES_BY_STATUS, new Object[] { status }, new InsurancePackageRowMapper());
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<InsurancePackage> getAllInsurancePackagesByAge(int age) {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(GET_FILTERED_PACKAGES_BY_AGE, new Object[] { age }, new InsurancePackageRowMapper());
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<DiseaseDetails> getDiseasesByPackageId(int id) {
		// TODO Auto-generated method stub
		return jdbcTemplate.query(GET_DISEASE_DETAILS_BY_PACKAGE_ID, new Object[] { id }, new DiseseDetailsRowMapper());

	}

	@Override
	public int addDisease(String name, String iCDCode, String description, String status,int inspid) {
	    String sql = "INSERT INTO Diseases (disc_name, disc_icdcode, disc_desc, disc_status) VALUES (?, ?, ?, ?) RETURNING disc_id";

	    try {
	        // Execute the query and retrieve the generated disease ID
	        int disid= jdbcTemplate.queryForObject(sql, Integer.class, name, iCDCode, description, status);
	        String sql1="insert into InsurancePackageCoveredDiseases values(?,?)";
	        return jdbcTemplate.update(sql1, inspid,disid);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1; // Return a negative value or handle the error as needed
	    }
	}

	@Override
	public int deleteDisease(int did, int inspid) {
		try{
			String sql = "delete from InsurancePackageCoveredDiseases where insp_id=? and disc_id=? ";
		String sql1 = "delete from Diseases where disc_id=?";
		jdbcTemplate.update(sql,did,inspid);
		
		jdbcTemplate.update(sql1,did);
		return 1;
		
	}
		catch (Exception e) {
	        e.printStackTrace();
	        return -1; // Return a negative value or handle the error as needed
	    }
	}

	@Override
	public String editDisease(String name, String iCDCode, String description, String status) {
		 String sql = "UPDATE Diseases SET disc_icdcode = ?, disc_desc = ?, disc_status = ? WHERE disc_name = ?";
	        try {
	            int rowsUpdated = jdbcTemplate.update(sql, iCDCode, description, status, name);
	            if (rowsUpdated > 0) {
	                return "Disease Updated Successfully..!!";
	            } else {
	                return "No disease found with the provided ID.";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Failed to update disease: " + e.getMessage();
	        }
	    }
	}
	
	
	
	
	

