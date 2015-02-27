/**
 * @author XiongJie, Date: 13-10-25
 */
package net.happyonroad.cache.support;

import net.happyonroad.cache.ListContainer;

import java.util.ArrayList;
import java.util.List;

class DefaultListContainer implements ListContainer {

    List<byte[]> values = new ArrayList<byte[]>();

    @Override
    public synchronized String popLeft() {
        if (values.isEmpty())
            return null;
        String ret = asString(values.get(0));
        values.remove(0);
        return ret;
    }

    @Override
    public synchronized String popRight() {
        if (values.isEmpty())
            return null;
        String ret = asString(values.get(values.size() - 1));
        values.remove(values.size() - 1);
        return ret;
    }

    @Override
    public synchronized String blockPopLeft(int timeout) {
        if (values.isEmpty()) {
            try {
                this.wait(timeout*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (values.isEmpty())
                return null;
        }
        String ret = asString(values.get(0));
        values.remove(0);
        this.notify();
        return ret;
    }

    @Override
    public synchronized byte[] blockPopRight(int timeout) {
        if (values.isEmpty()) {
            try {
                this.wait(timeout*1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            if (values.isEmpty())
                return null;
        }
        byte[] ret = values.get(values.size() - 1);
        values.remove(values.size() - 1);
        this.notify();
        return ret;
    }

    @Override
    public synchronized void pushLeft(byte[] value) {
        values.add(0, value);
        this.notify();
    }

    @Override
    public synchronized void pushRight(String value) {
        values.add(toBytes(value));
        this.notify();
    }

    @Override
    public synchronized void pushRight(List<String> value) {
        values.addAll(toBytesArray(value));
        this.notify();
    }

    @Override
    public synchronized boolean trim(int start, int end) {
        if(start < 0 )
            start = values.size() - start;

        if(end < 0 )
            end = values.size() + end;


        List<byte[]> new_values = new ArrayList<byte[]>();
        for(int i = start; i <= end; ++i) {
            new_values.add(values.get(i));
        }
        values = new_values;
        this.notify();
        return true;
    }

    @Override
    public String[] subList(int start, int end) {
        if(start < 0 )
            start = values.size() - start;

        if(end < 0 )
            end = values.size() - end;
        if( end > values.size() - start )
            end = values.size() - start ;

        ArrayList<byte[]> newValues = new ArrayList<byte[]>();
        for(int i = start; i < end; ++i) {
            newValues.add(values.get(i));
        }

        return toStringArray(newValues);
    }

    @Override
    public synchronized String[] toArray() {
        return toStringArray(values);
    }


    @Override
    public synchronized int size() {
        return values.size();
    }

    private String asString(byte[] bytes) {
        return new String(bytes);
    }

    private byte[] toBytes(String string){
        return string.getBytes();
    }

    private List<byte[]> toBytesArray(List<String> values) {
        List<byte[]> result = new ArrayList<byte[]>(values.size());
        for (String string : values) {
            result.add(toBytes(string));
        }
        return result;
    }

    private String[] toStringArray(List<byte[]> values) {
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            byte[] bytes = values.get(i);
            result[i] = asString(bytes);
        }
        return result;
    }

}
