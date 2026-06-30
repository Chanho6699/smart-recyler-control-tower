package com.chanho.smartrecycler.bin.repository;

import com.chanho.smartrecycler.bin.entity.Bin;
import com.chanho.smartrecycler.bin.entity.BinType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BinRepository extends JpaRepository<Bin, Long> {

    Optional<Bin> findByDeviceIdAndBinType(String deviceId, BinType binType);

    List<Bin> findAllByDeviceIdOrderByBinTypeAsc(String deviceId);

    List<Bin> findAllByOrderByUpdatedAtDesc();
}
