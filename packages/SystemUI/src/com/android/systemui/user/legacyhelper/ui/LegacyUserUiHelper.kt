/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.systemui.user.legacyhelper.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.systemui.R
import com.android.systemui.user.data.source.UserRecord
import kotlin.math.ceil

/**
 * Defines utility functions for helping with legacy UI code for users.
 *
 * We need these to avoid code duplication between logic inside the UserSwitcherController and in
 * modern architecture classes such as repositories, interactors, and view-models. If we ever
 * simplify UserSwitcherController (or delete it), the code here could be moved into its call-sites.
 */
object LegacyUserUiHelper {

    /** Returns the maximum number of columns for user items in the user switcher. */
    fun getMaxUserSwitcherItemColumns(userCount: Int): Int {
        // TODO(b/243844097): remove this once we remove the old user switcher implementation.
        return if (userCount < 5) {
            4
        } else {
            ceil(userCount / 2.0).toInt()
        }
    }

    @JvmStatic
    @DrawableRes
    fun getUserSwitcherActionIconResourceId(
        isAddUser: Boolean,
        isGuest: Boolean,
        isAddSupervisedUser: Boolean,
    ): Int {
        return if (isAddUser) {
            R.drawable.ic_add
        } else if (isGuest) {
            R.drawable.ic_account_circle
        } else if (isAddSupervisedUser) {
            R.drawable.ic_add_supervised_user
        } else {
            R.drawable.ic_avatar_user
        }
    }

    @JvmStatic
    fun getUserRecordName(
        context: Context,
        record: UserRecord,
        isGuestUserAutoCreated: Boolean,
        isGuestUserResetting: Boolean,
    ): String {
        val resourceId: Int? = getGuestUserRecordNameResourceId(record)
        return when {
            resourceId != null -> context.getString(resourceId)
            record.info != null -> record.info.name
            else ->
                context.getString(
                    getUserSwitcherActionTextResourceId(
                        isGuest = record.isGuest,
                        isGuestUserAutoCreated = isGuestUserAutoCreated,
                        isGuestUserResetting = isGuestUserResetting,
                        isAddUser = record.isAddUser,
                        isAddSupervisedUser = record.isAddSupervisedUser,
                    )
                )
        }
    }

    /**
     * Returns the resource ID for a string for the name of the guest user.
     *
     * If the given record is not the guest user, returns `null`.
     */
    @StringRes
    fun getGuestUserRecordNameResourceId(record: UserRecord): Int? {
        return when {
            record.isGuest && record.isCurrent ->
                com.android.settingslib.R.string.guest_exit_quick_settings_button
            record.isGuest && record.info != null -> com.android.internal.R.string.guest_name
            else -> null
        }
    }

    @JvmStatic
    @StringRes
    fun getUserSwitcherActionTextResourceId(
        isGuest: Boolean,
        isGuestUserAutoCreated: Boolean,
        isGuestUserResetting: Boolean,
        isAddUser: Boolean,
        isAddSupervisedUser: Boolean,
    ): Int {
        check(isGuest || isAddUser || isAddSupervisedUser)

        return when {
            isGuest && isGuestUserAutoCreated && isGuestUserResetting ->
                com.android.settingslib.R.string.guest_resetting
            isGuest && isGuestUserAutoCreated -> com.android.internal.R.string.guest_name
            isGuest -> com.android.internal.R.string.guest_name
            isAddUser -> com.android.settingslib.R.string.user_add_user
            isAddSupervisedUser -> R.string.add_user_supervised
            else -> error("This should never happen!")
        }
    }

    /** Alpha value to apply to a user view in the user switcher when it's selectable. */
    const val USER_SWITCHER_USER_VIEW_SELECTABLE_ALPHA = 1.0f

    /** Alpha value to apply to a user view in the user switcher when it's not selectable. */
    const val USER_SWITCHER_USER_VIEW_NOT_SELECTABLE_ALPHA = 0.38f
}
