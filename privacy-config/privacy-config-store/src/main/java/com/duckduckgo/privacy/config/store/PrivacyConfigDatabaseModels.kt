/*
 * Copyright (c) 2021 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.privacy.config.store

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.duckduckgo.feature.toggles.api.FeatureException
import com.duckduckgo.privacy.config.api.GpcException
import com.duckduckgo.privacy.config.api.GpcHeaderEnabledSite
import com.duckduckgo.privacy.config.api.PrivacyConfigData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "tracker_allowlist")
data class TrackerAllowlistEntity(
    @PrimaryKey val domain: String,
    val rules: List<AllowlistRuleEntity>,
)

class AllowlistRuleEntity(
    val rule: String,
    val domains: List<String>,
    val reason: String,
)

class RuleTypeConverter {

    @TypeConverter
    fun toRules(value: String): List<AllowlistRuleEntity> {
        return Adapters.ruleListAdapter.fromJson(value)!!
    }

    @TypeConverter
    fun fromRules(value: List<AllowlistRuleEntity>): String {
        return Adapters.ruleListAdapter.toJson(value)
    }
}

@Entity(tableName = "drm_exceptions")
data class DrmExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

fun DrmExceptionEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

@Entity(tableName = "unprotected_temporary")
data class UnprotectedTemporaryEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

@Entity(tableName = "https_exceptions")
data class HttpsExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

fun HttpsExceptionEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

@Entity(tableName = "gpc_header_enabled_sites")
data class GpcHeaderEnabledSiteEntity(@PrimaryKey val domain: String)

fun GpcHeaderEnabledSiteEntity.toGpcHeaderEnabledSite(): GpcHeaderEnabledSite {
    return GpcHeaderEnabledSite(domain = this.domain)
}

@Entity(tableName = "gpc_exceptions")
data class GpcExceptionEntity(@PrimaryKey val domain: String)

fun GpcExceptionEntity.toGpcException(): GpcException {
    return GpcException(domain = this.domain)
}

@Entity(tableName = "gpc_content_scope_config")
data class GpcContentScopeConfigEntity(
    @PrimaryKey val id: Int = 1,
    val config: String,
)

@Entity(tableName = "content_blocking_exceptions")
data class ContentBlockingExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

fun ContentBlockingExceptionEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

@Entity(tableName = "privacy_config")
data class PrivacyConfig(
    @PrimaryKey val id: Int = 1,
    val version: Long,
    val readme: String,
    val eTag: String?,
    val timestamp: String?,
)

fun PrivacyConfig.toPrivacyConfigData(): PrivacyConfigData {
    return PrivacyConfigData(version = this.version.toString(), eTag = this.eTag)
}

@Entity(tableName = "amp_link_formats")
data class AmpLinkFormatEntity(
    @PrimaryKey val format: String,
)

@Entity(tableName = "amp_keywords")
data class AmpKeywordEntity(
    @PrimaryKey val keyword: String,
)

@Entity(tableName = "amp_exceptions")
data class AmpLinkExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

@Entity(tableName = "tracking_parameters")
data class TrackingParameterEntity(
    @PrimaryKey val parameter: String,
)

@Entity(tableName = "tracking_parameter_exceptions")
data class TrackingParameterExceptionEntity(
    @PrimaryKey val domain: String,
    val reason: String,
)

fun AmpLinkExceptionEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

fun TrackingParameterExceptionEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

fun UnprotectedTemporaryEntity.toFeatureException(): FeatureException {
    return FeatureException(domain = this.domain, reason = this.reason)
}

class Adapters {
    companion object {
        private val moshi = Moshi.Builder().build()
        private val ruleListType =
            Types.newParameterizedType(List::class.java, AllowlistRuleEntity::class.java)
        val ruleListAdapter: JsonAdapter<List<AllowlistRuleEntity>> = moshi.adapter(ruleListType)
    }
}
