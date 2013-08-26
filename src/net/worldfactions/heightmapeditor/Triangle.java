
package net.worldfactions.heightmapeditor;

/**
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
* 
* Version 0.1
* Created by WorldFactions.net
* For feature implementation suggestions please visit https://www.facebook.com/WorldFactions
**/

import java.util.*;
public class Triangle
{
    public ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();
    public Vector3D normal;
    public Vector3D color;
    public Triangle(Vector3D v1, Vector3D v2, Vector3D v3, Vector3D norm, Vector3D col)
    {
        vectors.add(v1);
        vectors.add(v2);
        vectors.add(v3);
        normal = norm;
        color = col;
    }
}
