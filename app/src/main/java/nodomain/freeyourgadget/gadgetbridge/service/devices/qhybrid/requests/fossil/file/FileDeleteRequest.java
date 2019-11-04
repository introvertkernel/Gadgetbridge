package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file;

import android.bluetooth.BluetoothGattCharacteristic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.FossilRequest;

public class FileDeleteRequest extends FossilRequest {
    private boolean finished = false;
    private short handle;

    public FileDeleteRequest(short handle) {
        this.handle = handle;

        ByteBuffer buffer = createBuffer();

        buffer.putShort(handle);

        this.data = buffer.array();
    }

    @Override
    public void handleResponse(BluetoothGattCharacteristic characteristic) {
        super.handleResponse(characteristic);
        if(!characteristic.getUuid().toString().equals("3dda0003-957f-7d4a-34a6-74696673696d"))
            throw new RuntimeException("wrong response UUID");
        byte[] value = characteristic.getValue();

        if(value.length != 4) throw new RuntimeException("wrong response length");

        if(value[0] != (byte) 0x8B) throw new RuntimeException("wrong response start");

        ByteBuffer buffer = ByteBuffer.wrap(value);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if(buffer.getShort(1) != this.handle) throw new RuntimeException("wrong response handle");

        if(buffer.get(3) != 0) throw new RuntimeException("wrong response status: " + buffer.get(3));

        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public byte[] getStartSequence() {
        return new byte[]{(byte) 0x0B};
    }

    @Override
    public int getPayloadLength() {
        return 3;
    }
}
