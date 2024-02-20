import React, { useState } from 'react';
import FriendRecommendation from '../FriendRecommend';
import friendRecommendations from '../../data/data.js';

const DashboardFriendRecommend = () => {
  const [index, setIndex] = useState(0);

  const handlePrevClick = () => {
    const newIndex = Math.max(0, index - 1);
    setIndex(newIndex);
  };

  const handleNextClick = () => {
    const newIndex = Math.min(index + 1, friendRecommendations.length - 1);
    setIndex(newIndex);
  };

  const visibleFriendsData = friendRecommendations.slice(index, index + 5);

  return (
    <div className="w-1100 overflow-hidden mx-19">
      <p className='text-2xl font-bold mb-6'>Friend Recommendation</p>
      <div className="flex relative">
        {visibleFriendsData.map((friend) => (
          <FriendRecommendation key={friend.id} friend={friend} />
        ))}
        <div className="absolute top-28 left-0 right-3 flex justify-between items-center px-4 py-2">
          <button className='bg-slate-200 hover:bg-slate-300 h-16 w-16 rounded-full text-4xl' onClick={handlePrevClick} disabled={index === 0}>{'<'}</button>
          <button className='bg-slate-200 hover:bg-slate-300 h-16 w-16 rounded-full text-4xl' onClick={handleNextClick} disabled={index >= friendRecommendations.length - 5}>{'>'}</button>
        </div>
      </div>

    </div>
  );
};

export default DashboardFriendRecommend;
