package com.cole.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cole.service.MahasiswaService;
import com.cole.service.UserProfileService;
import com.cole.vo.Mahasiswa;
import com.cole.vo.Result;
import com.cole.vo.UserTokenInfo;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MahasiswaController {

	@Autowired
	MahasiswaService mahasiswaService;
	@Autowired
	UserProfileService userProfileService;

	// GET Mahasiswa BY ID API
	@GetMapping("/mahasiswa/{id}")
	public Mahasiswa getMahasiswa(@PathVariable("id") Long id_mhs) {
		Mahasiswa mahasiswa = mahasiswaService.getMahasiswa(id_mhs);
		return mahasiswa;
	}

	// GET List MAHASISWA API
	@GetMapping("/mahasiswas")
	public List<Mahasiswa> getMahasiswas() {
		List<Mahasiswa> mahasiswas = mahasiswaService.getMahasiswas();
		return mahasiswas;
	}

	// Login Mahasiswa API
	@PostMapping("/mahasiswa/login")
	public Object loginMahasiswa(HttpServletResponse response, @RequestBody Mahasiswa mahasiswaParam) {
		Mahasiswa mahasiswa = mahasiswaService.loginMahasiswa(mahasiswaParam.getEmail(), mahasiswaParam.getPassword());
		if (mahasiswa != null) {
			Long userId = mahasiswa.getId_mhs();
			return new Result(200, "Success", userId);
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new Result(401, "Incorect Email or Password");
		}
	}

	// login & registrasi
	@PostMapping("/mahasiswa")
	public ResponseEntity<Object> savePost(
			HttpServletResponse response,
			@RequestBody Mahasiswa mahasiswaParam,
			@RequestHeader("Authorization") String authorizationHeader) {
		if (authorizationHeader == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new Result(401, "Authorization header is missing"));
		}
		String userToken = authorizationHeader.replace("Bearer ", "");
		// Fetch user information from the Google API using the access_token
		UserTokenInfo userTokenInfo = userProfileService.fetchUserProfile(userToken);

		if (userTokenInfo == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Result(500, "Failed to fetch user profile"));
		}
		System.out.println(userTokenInfo.getEmail());
		// Check if the email exists
		Mahasiswa existingMahasiswa = mahasiswaService.getMahasiswaByEmail(userTokenInfo.getEmail());

		if (existingMahasiswa != null) {
			// Email exists, return the data
			return ResponseEntity.ok(existingMahasiswa);
		}
		Mahasiswa mahasiswa = new Mahasiswa(userTokenInfo.getName(), userTokenInfo.getName(),
				userTokenInfo.getEmail(), mahasiswaParam.getPassword(),
				mahasiswaParam.getTanggal_lahir(), mahasiswaParam.getLocation(), mahasiswaParam.getAbout(),
				mahasiswaParam.getKampus(),
				mahasiswaParam.getJurusan(), mahasiswaParam.getSemester(), userToken,
				userTokenInfo.getPicture());
		int saveResult = mahasiswaService.saveMahasiswa(mahasiswa);

		if (saveResult == 1) {
			return ResponseEntity.ok().body(new Result(200, "Success"));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Result(500, "Failed to register"));
		}
	}

	// EDIT MAHASISWA BY ID API
	@PutMapping("/mahasiswa/{id}")
	public Object modifyMahasiswa(HttpServletResponse response, @PathVariable("id") Long id_mhs,
			@RequestBody Mahasiswa mahasiswaParam) {
		Mahasiswa mahasiswa = new Mahasiswa(id_mhs, mahasiswaParam.getNama(), mahasiswaParam.getUsername(),
				mahasiswaParam.getEmail(), mahasiswaParam.getPassword(),
				mahasiswaParam.getTanggal_lahir(), mahasiswaParam.getLocation(), mahasiswaParam.getAbout(),
				mahasiswaParam.getKampus(), mahasiswaParam.getJurusan(), mahasiswaParam.getSemester(),
				mahasiswaParam.getToken(), mahasiswaParam.getProfileUrl());

		boolean isSuccess = mahasiswaService.updateMahasiswa(mahasiswa);
		if (isSuccess) {
			return new Result(200, "Success");
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new Result(500, "Fail");
		}
	}

}
