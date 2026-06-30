package com.chanho.smartrecycler.bin.service;

import com.chanho.smartrecycler.bin.dto.BinResetRequest;
import com.chanho.smartrecycler.bin.dto.BinResponse;
import com.chanho.smartrecycler.bin.entity.Bin;
import com.chanho.smartrecycler.bin.entity.BinType;
import com.chanho.smartrecycler.bin.repository.BinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BinService {

    private static final int DEFAULT_CAPACITY = 100;

    private final BinRepository binRepository;

    public BinService(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    @Transactional
    public BinResponse increaseBinCount(String deviceId, String targetBin) {
        BinType binType = parseBinType(targetBin);

        Bin bin = binRepository.findByDeviceIdAndBinType(deviceId, binType)
                .orElseGet(() -> new Bin(deviceId, binType, DEFAULT_CAPACITY));

        bin.increaseCount();

        Bin savedBin = binRepository.save(bin);
        return new BinResponse(savedBin);
    }

    @Transactional
    public BinResponse resetBin(BinResetRequest request) {
        BinType binType = parseBinType(request.getBinType());

        Bin bin = binRepository.findByDeviceIdAndBinType(request.getDeviceId(), binType)
                .orElseGet(() -> new Bin(request.getDeviceId(), binType, DEFAULT_CAPACITY));

        bin.reset();

        Bin savedBin = binRepository.save(bin);
        return new BinResponse(savedBin);
    }

    @Transactional(readOnly = true)
    public List<BinResponse> getBins() {
        return binRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(BinResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BinResponse> getBinsByDeviceId(String deviceId) {
        return binRepository.findAllByDeviceIdOrderByBinTypeAsc(deviceId)
                .stream()
                .map(BinResponse::new)
                .toList();
    }

    private BinType parseBinType(String targetBin) {
        try {
            return BinType.valueOf(targetBin.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return BinType.UNKNOWN;
        }
    }
}
