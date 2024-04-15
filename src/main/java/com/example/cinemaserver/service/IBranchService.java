package com.example.cinemaserver.service;

import com.example.cinemaserver.request.BranchRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.response.BranchResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IBranchService {

    List<Branch> getAll();

    String getBranchPhoto(Branch branch) throws SQLException;

    void addNewBranch(Long areaId,BranchRequest branchRequest) throws IOException, SQLException;

    ResponseEntity<String> deleteBranch(Long id);

    Branch getBranch(Long id);

    BranchResponse getBranchResponse(Branch branch) throws SQLException;

    Branch updateBranch(Long id, BranchRequest branchRequest) throws IOException, SQLException;

    List<Branch> getBranchClientByMovieIdAndAreaId(Long movieId,Long areaId);

    List<Branch> getBranchByAreaId(Long areaId);

    BranchResponse getBranchResponseNonePhoto(Branch branch) throws SQLException;
}
