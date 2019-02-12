package com.zegreatrob.coupling.server.entity.user

import com.zegreatrob.coupling.common.entity.tribe.TribeId

data class User(val email: String, val authorizedTribeIds: List<TribeId>)