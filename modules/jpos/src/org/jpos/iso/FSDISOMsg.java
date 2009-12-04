/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2009 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.iso;

import org.jpos.util.FSDMsg;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

public class FSDISOMsg extends ISOMsg implements Cloneable  {
    FSDMsg fsd;
    public  FSDISOMsg () {
        super();
    }
    public FSDISOMsg (FSDMsg fsd) {
        super();
        this.fsd = fsd;
    }
    public String getMTI() {
        return getString(0);
    }
    public byte[] pack() throws ISOException {
        try {
            return fsd.packToBytes();
        } catch (Exception e) {
            throw new ISOException (e);
        }
    }
    public int unpack(byte[] b) throws ISOException {
        try {
            fsd.unpack (b);
            return b.length;
        } catch (Exception e) {
            throw new ISOException (e);
        }
    }
    public FSDMsg getFSDMsg() {
        return fsd;
    }
    public String getString (int fldno) {
        return fsd.get (Integer.toString(fldno));
    }
    public boolean hasField (int fldno) {
        return getString(fldno) != null;
    }
    public void dump (PrintStream p, String indent) {
        if (fsd != null)
            fsd.dump (p, indent);
    }
    public void writeExternal (ObjectOutput out) throws IOException {
        out.writeByte (0);  // reserved for future expansion (version id)
        out.writeUTF (fsd.getBasePath());
        out.writeUTF (fsd.getBaseSchema());
        out.writeObject (fsd.getMap());
    }
    public void readExternal  (ObjectInput in) 
        throws IOException, ClassNotFoundException
    {
        in.readByte();  // ignore version for now
        String basePath = in.readUTF();
        String baseSchema = in.readUTF();
        fsd = new FSDMsg (basePath, baseSchema);
        Map map = (Map) in.readObject();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            fsd.set ((String) entry.getKey(), (String) entry.getValue());
        }
    }
    public Object clone() {
        FSDISOMsg m = (FSDISOMsg) super.clone();
        m.fsd = (FSDMsg) fsd.clone();
        return m;
    }
    public Object clone(int[] fields) {
        FSDISOMsg m = (FSDISOMsg) super.clone();
        m.fsd = new FSDMsg(fsd.getBasePath(), fsd.getBaseSchema());
        for (int i=0; i<fields.length; i++) {
            String f = Integer.toString(fields[i]);
            m.fsd.set (f, fsd.get (f));
        }
        return m;
    }
    public void merge (ISOMsg m) {
        if (m instanceof FSDISOMsg) {
            fsd.merge (((FSDISOMsg)m).getFSDMsg());
        } else {
            for (int i=0; i<=m.getMaxField(); i++) {
                if (m.hasField(i))
                    fsd.set (Integer.toString(i), m.getString(i));
            }
        }
    }
    public void setResponseMTI() {
        try {
            super.setResponseMTI();
        } catch (ISOException ignored) { }               
    }
    private static final long serialVersionUID = 1L;
}
