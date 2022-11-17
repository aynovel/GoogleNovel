package life.forever.cf.entry;

import android.text.TextUtils;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;


public class BeanParser {

    public static void parseUserInfo(JSONObject object) {
        AppUser user = PlotRead.getAppUser();
        JSONObject base = JSONUtil.getJSONObject(object, "base");
        JSONObject finance = JSONUtil.getJSONObject(object, "finance");
        JSONObject sign = JSONUtil.getJSONObject(object, "sign");
        JSONObject signData = JSONUtil.getJSONObject(sign, "sign");
        JSONObject message = JSONUtil.getJSONObject(object, "message");

        user.head = JSONUtil.getString(base, "avatar_url");
        user.nickName = JSONUtil.getString(base, "nickname");
        user.sex = JSONUtil.getInt(base, "sex");
        user.birthday = JSONUtil.getString(base, "birthday");
        user.is_author_user = JSONUtil.getString(base, "is_author_user");
        user.signature = JSONUtil.getString(base, "signature");
        user.level = JSONUtil.getInt(base, "level");
        user.vip = JSONUtil.getInt(finance, "vip_level");
        user.money = JSONUtil.getInt(finance, "money");
        user.voucher = JSONUtil.getInt(finance, "voucher");
        user.monthVip = JSONUtil.getInt(finance, "is_month") == 1;
        user.monthDate = JSONUtil.getInt(finance, "month_end");
        user.messageTag = JSONUtil.getInt(message, "total") != 0;
        user.messageTotal = JSONUtil.getInt(message, "total");
        user.signDays = JSONUtil.getInt(sign, "continue");
        user.order_discount = JSONUtil.getString(object, "order_discount");
        user.author_message= JSONUtil.getString(object, "author_message");
        if (JSONUtil.getInt(signData, String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))) == 1) {
            user.signDate = ComYou.currentTimeFormat(Constant.DATE_FORMATTER_1);
        } else { // 当天未签到
            user.signDate = "";
        }
    }

    public static Work getFirstRrcWork(JSONObject object) {
        Work work = new Work();
        if (!ShelfUtil.exist(JSONUtil.getInt(object, "wid"))) {
            work.wid = JSONUtil.getInt(object, "wid");
            work.wtype = JSONUtil.getInt(object, "type");
            work.cover = JSONUtil.getString(object, "h_url");
            work.title = JSONUtil.getString(object, "title");
            work.author = JSONUtil.getString(object, "author");
            work.description = JSONUtil.getString(object, "description");
            work.sortTitle = JSONUtil.getString(object, "sort");
            if (TextUtils.isEmpty(work.sortTitle)) {
                work.sortTitle = JSONUtil.getString(object, "sort_title");
            }
            work.updatetime = JSONUtil.getInt(object, "update_time");
            work.isfinish = JSONUtil.getInt(object, "is_finish");
            work.totalWord = JSONUtil.getInt(object, "word_total");
            work.totalChapter = JSONUtil.getInt(object, "counts");
            work.score = (int) JSONUtil.getDouble(object, "score");
            work.award_total = (int) JSONUtil.getDouble(object, "award_total");
            work.pv = JSONUtil.getInt(object, "pv");
            work.platform = JSONUtil.getString(object, "cp_name");
            work.recId = JSONUtil.getInt(object, "rec_id");
            work.isvip = JSONUtil.getString(object, "is_vip");
            work.lasttime = ComYou.currentTimeSeconds();
            work.lastChapterOrder = JSONUtil.getInt(object, "lastChapterPos");
            work.lastChapterId = Constant.ZERO;
            work.lastChapterPosition = Constant.ZERO;
            work.is_rec = JSONUtil.getInt(object, "is_rec");
        }
        return work;
    }

    public static Work getWork(JSONObject object) {
        Work work = new Work();
        work.wid = JSONUtil.getInt(object, "wid");
        work.wtype = JSONUtil.getInt(object, "type");
        work.cover = JSONUtil.getString(object, "h_url");
        work.title = JSONUtil.getString(object, "title");
        work.author = JSONUtil.getString(object, "author");
        work.description = JSONUtil.getString(object, "description");
        work.sortTitle = JSONUtil.getString(object, "sort");
        if (TextUtils.isEmpty(work.sortTitle)) {
            work.sortTitle = JSONUtil.getString(object, "sort_title");
        }
        work.updatetime = JSONUtil.getInt(object, "update_time");
        work.isfinish = JSONUtil.getInt(object, "is_finish");
        work.totalWord = JSONUtil.getInt(object, "word_total");
        work.totalChapter = JSONUtil.getInt(object, "counts");
        work.score = (int) JSONUtil.getDouble(object, "score");
        work.award_total = (int) JSONUtil.getDouble(object, "award_total");
        work.pv = JSONUtil.getInt(object, "pv");
        work.platform = JSONUtil.getString(object, "cp_name");
        work.recId = JSONUtil.getInt(object, "rec_id");
        work.isvip = JSONUtil.getString(object, "is_vip");
        work.lasttime = ComYou.currentTimeSeconds();
        work.lastChapterOrder = JSONUtil.getInt(object, "lastChapterPos");
        work.lastChapterId = Constant.ZERO;
        work.lastChapterPosition = Constant.ZERO;

        return work;
    }

    public static LatestChapter getLatestChapter(JSONObject object) {
        LatestChapter work = new LatestChapter();
        work.wid = JSONUtil.getInt(object, "wid");
        work.cid = JSONUtil.getInt(object, "id");
        work.title = JSONUtil.getString(object, "title");
        work.updatetime = JSONUtil.getInt(object, "update_time");
        return work;
    }

    public static RewardInfo getReward(JSONObject object) {
        RewardInfo rewardInfo = new RewardInfo();
        rewardInfo.id = JSONUtil.getInt(object, "id");
        rewardInfo.money = JSONUtil.getInt(object, "money");
        rewardInfo.ruleId = JSONUtil.getInt(object, "rule_id");
        rewardInfo.coinType = JSONUtil.getInt(object, "coin_type");
        rewardInfo.defaultCheck = JSONUtil.getInt(object, "default");
        return rewardInfo;
    }

    public static MoreBuy getMoreBuy(JSONObject object) {
        MoreBuy mMoreBuy = new MoreBuy();
        mMoreBuy.count = JSONUtil.getInt(object, "count");
        mMoreBuy.origin = JSONUtil.getInt(object, "origin");
        mMoreBuy.discount = JSONUtil.getInt(object, "discount");
        mMoreBuy.is_discount = JSONUtil.getInt(object, "is_discount");
        mMoreBuy.discount_title = JSONUtil.getString(object, "discount_title");

        mMoreBuy.sum = JSONUtil.getInt(object, "sum");
        mMoreBuy.isclick = false;
        return mMoreBuy;
    }

    public static Catalog getCatalog(JSONObject object) {
        Catalog catalog = new Catalog();
        catalog.id = JSONUtil.getInt(object, "id");
        catalog.title = JSONUtil.getString(object, "title");
        catalog.sort = JSONUtil.getInt(object, "sort");
        catalog.isvip = JSONUtil.getInt(object, "isvip");
        catalog.vip = JSONUtil.getInt(object, "vip");
        catalog.createtime = JSONUtil.getInt(object, "create_time");
        catalog.updatetime = JSONUtil.getInt(object, "update_time");
        catalog.order = JSONUtil.getInt(object, "order");
        return catalog;
    }

    public static Voucher getVoucher(JSONObject object) {
        Voucher voucher = new Voucher();
        voucher.id = JSONUtil.getInt(object, "id");
        voucher.value = JSONUtil.getInt(object, "voucher");
        voucher.left = JSONUtil.getInt(object, "left");
        voucher.status = JSONUtil.getInt(object, "status");
        voucher.name = JSONUtil.getString(object, "name");
        voucher.endtime = JSONUtil.getInt(object, "end_time");
        return voucher;
    }

    public static Task getTask(JSONObject object) {
        Task task = new Task();
        task.id = JSONUtil.getInt(object, "id");
        task.type = JSONUtil.getInt(object, "type");
        task.status = JSONUtil.getInt(object, "status");
        task.title = JSONUtil.getString(object, "title");
        task.reward = JSONUtil.getString(object, "reward");
        task.description = JSONUtil.getString(object, "description");
        task.experience = JSONUtil.getInt(object, "experience");
        task.giving = JSONUtil.getInt(object, "giving");
        task.givingType = JSONUtil.getInt(object, "giving_type");
        return task;
    }

    public static Comment getComment(JSONObject object) {
        Comment comment = new Comment();
        comment.id = JSONUtil.getInt(object, "id");
        comment.type = JSONUtil.getInt(object, "type");
        comment.title = JSONUtil.getString(object, "title");
        comment.content = JSONUtil.getString(object, "content");
        comment.contentType = JSONUtil.getInt(object, "content_type");
        comment.replyCount = JSONUtil.getInt(object, "reply_count");
        comment.likeCount = JSONUtil.getInt(object, "like_count");
        comment.addtime = JSONUtil.getInt(object, "addtime");
        comment.floor = JSONUtil.getInt(object, "floor");
        comment.isLike = JSONUtil.getInt(object, "is_like");

        JSONArray list = JSONUtil.getJSONArray(object, "reply");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                Comment reply = getComment(JSONUtil.getJSONObject(list, i));
                comment.replays.add(reply);
            }
        }

        comment.pid = JSONUtil.getInt(object, "pid");
        comment.puid = JSONUtil.getInt(object, "puid");
        comment.relateId = JSONUtil.getInt(object, "relate_id");
        comment.toUid = JSONUtil.getInt(object, "to_uid");
        comment.toName = JSONUtil.getString(object, "to_name");
        comment.wid = JSONUtil.getInt(object, "wid");
        comment.cid = JSONUtil.getInt(object, "cid");
        comment.score = (int) JSONUtil.getDouble(object, "score");
        comment.isTop = (int) JSONUtil.getDouble(object, "is_top");
        comment.isBanUser = (int) JSONUtil.getDouble(object, "is_ban_user");
        comment.uid = JSONUtil.getInt(object, "uid");
        comment.head = JSONUtil.getString(object, "avatar_url");
        comment.nickname = JSONUtil.getString(object, "nickname");
        comment.level = JSONUtil.getInt(object, "level");
        comment.fansLevel = JSONUtil.getInt(object, "fans_level");
        comment.fansName = JSONUtil.getString(object, "fans_name");
        comment.is_author_comment = JSONUtil.getInt(object, "is_author_comment");
        comment.is_author_user = JSONUtil.getInt(object, "is_author_user");
        comment.from = JSONUtil.getString(object, "from");
        comment.fromType = JSONUtil.getInt(object, "from_type");

        return comment;
    }

    public static Person getPerson(JSONObject object) {
        Person person = new Person();
        person.uid = JSONUtil.getInt(object, "uid");
        person.logo = JSONUtil.getString(object, "logo");
        person.nickname = JSONUtil.getString(object, "nickname");
        person.level = JSONUtil.getInt(object, "level");
        person.vipLevel = JSONUtil.getInt(object, "vip_level");
        person.honorid = JSONUtil.getInt(object, "honorid");
        person.honor = JSONUtil.getString(object, "honor");
        person.fansValue = JSONUtil.getInt(object, "fsval");
        return person;
    }

    public static RankType getRankType(JSONObject object) {
        RankType person = new RankType();
        person.id = JSONUtil.getInt(object, "id");
        person.title = JSONUtil.getString(object, "title");
        person.pageId = JSONUtil.getInt(object, "type");
        person.cycleId = JSONUtil.getInt(object, "sort");
        person.status = JSONUtil.getInt(object, "status");
        person.icon_image = JSONUtil.getString(object, "icon_image");
        person.icon_gray_image = JSONUtil.getString(object, "icon_gray_image");
        person.icon_type = JSONUtil.getString(object, "icon_type");
        person.desc = JSONUtil.getString(object, "desc");
        return person;
    }

    public static Message getMessage(JSONObject object) {
        Message person = new Message();
        person.id = JSONUtil.getInt(object, "id");
        person.title = JSONUtil.getString(object, "title");
        person.senderid = JSONUtil.getInt(object, "senderid");
        person.receiverid = JSONUtil.getInt(object, "receiverid");
        person.msg_id = JSONUtil.getInt(object, "msg_id");
        person.type = JSONUtil.getInt(object, "type");
        person.status = JSONUtil.getInt(object, "status");
        person.addtime = JSONUtil.getInt(object, "addtime");
        person.content = JSONUtil.getString(object, "content");
        person.url = JSONUtil.getString(object, "url");
        person.path = JSONUtil.getString(object, "path");
        return person;
    }
}
