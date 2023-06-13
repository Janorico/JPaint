/*
 * Free and open-source svg-editor.
 * Copyright (C) 2023 Janosch Lion
 *
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.janorico.jpaint.gui.theme;

import com.formdev.flatlaf.FlatDarkLaf;

public class JPaintTheme
        extends FlatDarkLaf {
    public static final String NAME = "JPaint Theme";

    public static boolean setup() {
        return setup(new JPaintTheme());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, JPaintTheme.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
